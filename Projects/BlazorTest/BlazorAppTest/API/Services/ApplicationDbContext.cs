using Microsoft.EntityFrameworkCore;
using API.Models;

namespace BlazorAppTest.Services
{
    public class ApplicationDbContext : DbContext
    {
        public ApplicationDbContext(DbContextOptions options) : base(options)
        {
        }

        public DbSet<Client> Clients { get; set; }
    }
}
