using Parse;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace RHSMustangsPR
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            ParseClient.Initialize("VTncYgZyAW6wV67VNiXAIAYzotG4EZaz0kfzPCbt", "MlsKCUqqoD7K0imrzz7vFydnuxBAJGGigdZYzDEp");

            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new SchedulePR());
        }
    }
}
