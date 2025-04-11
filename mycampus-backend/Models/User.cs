namespace mycampus_backend.Models
{
    public class User
    {
        public int Id { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string Email { get; set; }
        public string Country { get; set; }
        public string City { get; set; }
        public string Zip { get; set; }
        public string Street { get; set; }
        public string HouseNumber { get; set; }
        public DateTime BirthDate { get; set; }
        public string Role { get; set; }
        public string PasswordHash { get; set; }
    }
}
