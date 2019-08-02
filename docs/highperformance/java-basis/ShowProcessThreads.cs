using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace ShowProcessThreads
{
    class Program
    {
        static void Main(string[] args)
        {
            while (true)
            {
                try
                {
                    Console.WriteLine("please input the pid");
                    int pid = int.Parse(Console.ReadLine());
                    var process = Process.GetProcessById(pid);
                    foreach (ProcessThread thread in process.Threads)
                    {
                        Console.WriteLine("thread id:{0}\tpriority: {1}\tstate: {2}", thread.Id, thread.PriorityLevel,thread.ThreadState);
                    }
                }
                catch (Exception ex)
                {

                    Console.WriteLine(ex.Message);
                }
            }
        }
    }
}
