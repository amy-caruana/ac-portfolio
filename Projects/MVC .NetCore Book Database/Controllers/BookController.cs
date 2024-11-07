using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.Data.SqlClient;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using FinalAssignment.Data;
using FinalAssignment.Models;

namespace FinalAssignment.Controllers
{
    public class BookController : Controller
    {
        private readonly IConfiguration _configuration;

        public BookController(IConfiguration configuration)
        {
            this._configuration = configuration;
        }

        public IActionResult Index()
        {
            DataTable dtbl = new DataTable();
            using (SqlConnection sqlConnection = new SqlConnection(_configuration.GetConnectionString("DevConnection")))
            {
                sqlConnection.Open();
                SqlDataAdapter sqlDa = new SqlDataAdapter("BookViewAll", sqlConnection);
                sqlDa.SelectCommand.CommandType = CommandType.StoredProcedure;
                sqlDa.Fill(dtbl);
            }
            return View(dtbl);
        }

        public IActionResult AddOrEdit(int? id)
        {
            BookModel bookModel = new BookModel();
            if (id > 0)
                bookModel = FetchBookByID(id);
            return View(bookModel);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public IActionResult AddOrEdit(int id, [Bind("ID,ISBN,BookName,Genre,Author")] BookModel bookModel)
        {
            if (ModelState.IsValid)
            {
                using (SqlConnection sqlConnection = new SqlConnection(_configuration.GetConnectionString("DevConnection")))
                {
                    sqlConnection.Open();
                    SqlCommand sqlCmd = new SqlCommand("BookAddOrEdit", sqlConnection);
                    sqlCmd.CommandType = CommandType.StoredProcedure;
                    sqlCmd.Parameters.AddWithValue("ID", bookModel.ID);
                    sqlCmd.Parameters.AddWithValue("ISBN", bookModel.ISBN);
                    sqlCmd.Parameters.AddWithValue("BookName", bookModel.BookName);
                    sqlCmd.Parameters.AddWithValue("Genre", bookModel.Genre);
                    sqlCmd.Parameters.AddWithValue("Author", bookModel.Author);
                    sqlCmd.ExecuteNonQuery();
                }
                return RedirectToAction(nameof(Index));
            }
            return View(bookModel);
        }

        public IActionResult Delete(int? id)
        {
            BookModel bookModel = FetchBookByID(id);
            return View(bookModel);
        }

        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public IActionResult DeleteConfirmed(int id)
        {
            using (SqlConnection sqlConnection = new SqlConnection(_configuration.GetConnectionString("DevConnection")))
            {
                sqlConnection.Open();
                SqlCommand sqlCmd = new SqlCommand("BookDeleteByID", sqlConnection);
                sqlCmd.CommandType = CommandType.StoredProcedure;
                sqlCmd.Parameters.AddWithValue("ID", id);
                sqlCmd.ExecuteNonQuery();
            }
            return RedirectToAction(nameof(Index));
        }

        [NonAction]
        public BookModel FetchBookByID(int? id)
        {
            BookModel bookModel = new BookModel();
            using (SqlConnection sqlConnection = new SqlConnection(_configuration.GetConnectionString("DevConnection")))
            {
                DataTable dtbl = new DataTable();
                sqlConnection.Open();
                SqlDataAdapter sqlDa = new SqlDataAdapter("BookViewByID", sqlConnection);
                sqlDa.SelectCommand.CommandType = CommandType.StoredProcedure;
                sqlDa.SelectCommand.Parameters.AddWithValue("ID", id);
                sqlDa.Fill(dtbl);
                if (dtbl.Rows.Count == 1)
                {
                    bookModel.ID = Convert.ToInt32(dtbl.Rows[0]["ID"].ToString());
                    bookModel.ISBN = Convert.ToInt32(dtbl.Rows[0]["ISBN"].ToString());
                    bookModel.BookName = dtbl.Rows[0]["BookName"].ToString();
                    bookModel.Genre = dtbl.Rows[0]["Genre"].ToString();
                    bookModel.Author = dtbl.Rows[0]["Author"].ToString();
                }
                return bookModel;
            }
        }
    }
}