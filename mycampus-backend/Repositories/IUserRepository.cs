using mycampus_backend.Models;

namespace mycampus_backend.Repositories
{
    public interface IUserRepository
    {
        Task<bool> UserExists(string email);
        Task<User> AddUser(User user);
    }
}
