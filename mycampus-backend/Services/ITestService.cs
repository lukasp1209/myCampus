using mycampus_backend.Models;
using mycampus_backend.Repositories;

namespace mycampus_backend.Services
{
    public interface ITestService
    {
        IEnumerable<TestModel> GetAllMessages();
    }

    public class TestService : ITestService
    {
        private readonly ITestRepository _repository;

        public TestService(ITestRepository repository)
        {
            _repository = repository;
        }

        public IEnumerable<TestModel> GetAllMessages()
        {
            return _repository.GetAll();
        }
    }
}
