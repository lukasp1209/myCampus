using mycampus_backend.Data;
using mycampus_backend.Models;

namespace mycampus_backend.Repositories
{
    public class UserRepository : IUserRepository
    {
        private readonly ApplicationDbContext _context;

        public UserRepository(ApplicationDbContext context)
        {
            _context = context;
        }

        public async Task<bool> UserExists(string email)
        {
            return _context.Users.Any(u => u.Email == email);
        }

        public async Task<User> AddUser(User user)
        {
            _context.Users.Add(user);
            await _context.SaveChangesAsync();
            return user;
        }
    }
}
