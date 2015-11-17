using NUnit.Framework;

namespace TestApp.Test
{
    [TestFixture]
    [Category("major")]
    class CalcTest9
    {
        [Test]
        public void minus_should_work()
        {
            Assert.That(4 - 1, Is.EqualTo(3), "Wrong result");
        }

        [Test]
        public void plus_should_work()
        {
            Assert.That(4 + 1, Is.EqualTo(4), "Wrong result");
        }
    }
}
