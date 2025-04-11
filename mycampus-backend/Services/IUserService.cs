using mycampus_backend.Models;

namespace mycampus_backend.Services
{
    public interface IUserService
    {
        Task<ApiResponse<object>> RegisterUser(UserRegistrationDto dto);
    }
}
