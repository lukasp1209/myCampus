using Microsoft.EntityFrameworkCore;
using mycampus_backend.Models; 

namespace mycampus_backend.Data
{
    public class ApplicationDbContext : DbContext
    {
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options)
            : base(options)
        {
        }

        // DbSets für deine Modelle
        public DbSet<User> Users { get; set; }
    }
}