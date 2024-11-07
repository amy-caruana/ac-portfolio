using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using FinalAssignment.Models;

namespace FinalAssignment.Data
{
    public class FinalAssignmentContext : DbContext
    {
        public FinalAssignmentContext (DbContextOptions<FinalAssignmentContext> options)
            : base(options)
        {
        }

        public DbSet<FinalAssignment.Models.BookModel> BookModel { get; set; } = default!;
    }
}
