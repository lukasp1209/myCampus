using Microsoft.AspNetCore.Identity;
using mycampus_backend.Models;
using mycampus_backend.Repositories;
using mycampus_backend.Utilities;

namespace mycampus_backend.Services
{
    public class UserService : IUserService
    {
        private readonly IUserRepository _userRepository;
        private readonly IPasswordHasher<User> _passwordHasher;

        public UserService(IUserRepository userRepository, IPasswordHasher<User> passwordHasher)
        {
            _userRepository = userRepository;
            _passwordHasher = passwordHasher;
        }

        public async Task<ApiResponse<object>> RegisterUser(UserRegistrationDto dto)
        {

            if (string.IsNullOrEmpty(dto.FirstName) || string.IsNullOrEmpty(dto.LastName) ||
                string.IsNullOrEmpty(dto.Email) || dto.Address == null ||
                dto.BirthDate == default || !IsValidRole(dto.Role))
            {
                return new ApiResponse<object>
                {
                    Metadata = new Metadata { Status = "error", Message = "Invalid input data" },
                    Payload = null
                };
            }

            // Überprüfen, ob der Benutzer bereits existiert
            if (await _userRepository.UserExists(dto.Email))
            {
                return new ApiResponse<object>
                {
                    Metadata = new Metadata { Status = "error", Message = "User already exists" },
                    Payload = null
                };
            }

            // Generieren eines sicheren, zufälligen Passworts
            var generatedPassword = PasswordGenerator.GeneratePassword();

            // Passwort vor dem Speichern hashen
            var user = new User
            {
                FirstName = dto.FirstName,
                LastName = dto.LastName,
                Email = dto.Email,
                Country = dto.Address.Country,
                City = dto.Address.City,
                Zip = dto.Address.Zip,
                Street = dto.Address.Street,
                HouseNumber = dto.Address.HouseNumber,
                BirthDate = dto.BirthDate,
                Role = dto.Role,
                PasswordHash = _passwordHasher.HashPassword(null, generatedPassword)
            };

            // Benutzer in der Datenbank speichern
            await _userRepository.AddUser(user);

            return new ApiResponse<object>
            {
                Metadata = new Metadata { Status = "success", Message = "User created successfully" },
                Payload = new { GeneratedPassword = generatedPassword }
            };
        }

        private bool IsValidRole(string role)
        {
            var allowedRoles = new[] { "Student", "Professor", "Admin" };
            return allowedRoles.Contains(role);
        }
    }
}
