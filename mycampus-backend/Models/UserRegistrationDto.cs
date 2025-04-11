namespace mycampus_backend.Models
{
    public class UserRegistrationDto
    {
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string Email { get; set; }
        public AddressDto Address { get; set; }
        public DateTime BirthDate { get; set; }
        public string Role { get; set; }
    }
}
