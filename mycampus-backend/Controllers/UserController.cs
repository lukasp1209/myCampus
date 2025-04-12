using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using mycampus_backend.Models;
using mycampus_backend.Services;

namespace mycampus_backend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class UserController : ControllerBase
    {
        private readonly IUserService _userService;

        public UserController(IUserService userService)
        {
            _userService = userService;
        }

        [HttpPost("register")]
        //[Authorize(Roles = "Admin")]
        public async Task<IActionResult> Register([FromBody] UserRegistrationDto dto)
        {
            var response = await _userService.RegisterUser(dto);
            if (response.Metadata.Status == "error")
            {
                return BadRequest(response);
            }

            return Ok(response);
        }
    }
}
