using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using mycampus_backend.Data;
using mycampus_backend.Models;
using mycampus_backend.Services;

namespace mycampus_backend.Controllers
{

    [ApiController]
    [Route("api/[controller]")]
    public class TestController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public TestController(ApplicationDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<IActionResult> Get()
        {
            var testModels = await _context.TestModels.ToListAsync();
            return Ok(testModels);
        }
    }
}
