using Microsoft.AspNetCore.Mvc;
using mycampus_backend.Models;
using mycampus_backend.Services;

namespace mycampus_backend.Controllers
{

    [ApiController]
    [Route("api/[controller]")]
    public class TestController : Controller
    {
        private readonly ITestService _testService;

        public TestController(ITestService testService)
        {
            _testService = testService;
        }

        [HttpGet]
        public ActionResult<IEnumerable<TestModel>> Get()
        {
            var messages = _testService.GetAllMessages();
            return Ok(messages);
        }
    }
}
