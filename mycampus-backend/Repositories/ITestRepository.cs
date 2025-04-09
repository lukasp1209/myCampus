using mycampus_backend.Models;

namespace mycampus_backend.Repositories
{
    public interface ITestRepository
    {
        IEnumerable<TestModel> GetAll();
    }

    public class TestRepository : ITestRepository
    {
        public IEnumerable<TestModel> GetAll()
        {
            return new List<TestModel>
            {
                new TestModel { Id = 1, Message = "Hello World!" }
            };
        }
    }
}
