using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace FinalAssignment.Models
{
    public class BookModel
    {
        [Key]
        public int ID { get; set; }
        [Required]
        public int ISBN { get; set; }
        [Required]
        public string BookName { get; set; }
        [Required]
        public string Genre { get; set; }
        [Required]
        public string Author { get; set; }
    }
}
