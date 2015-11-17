using NUnit.Framework;

namespace TestApp.Test
{
    [TestFixture]
    class CalcTest3
    {
        [Test]
        public void minus_should_work()
        {
            Assert.That(4 - 1, Is.EqualTo(3), "Wrong result");
        }

        [Test]
        public void plus_should_work()
        {
            Assert.That(4 + 1, Is.EqualTo(5), "Wrong result");
        }
    }
}
