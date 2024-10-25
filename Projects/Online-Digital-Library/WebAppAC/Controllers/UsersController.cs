using Microsoft.AspNetCore.Mvc;
using WebAppAC.Repositories;

namespace WebAppAC.Controllers
{
    public class UsersController : Controller
    {
        UsersRepository _usersRepository;
        public UsersController(UsersRepository usersRepository)
        {
            _usersRepository = usersRepository;

        }
        public async Task<IActionResult> Index(string documentId)
        {
            var list = await _usersRepository.GetUsers(documentId);
            return View(list);
        }

        public async Task<IActionResult> Read(string userId, string documentId)
        {
            var myUser = await _usersRepository.GetUser(documentId, userId);
            return View(myUser);
        }

    }
}
