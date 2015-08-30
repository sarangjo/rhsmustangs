using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace RHSMustangsPR
{
    public partial class SchedulePR : Form
    {
        public SchedulePR()
        {
            InitializeComponent();

            Console.WriteLine(Updates.DateToString(new DateTime(2015, 8, 15)));
        }

        private void baseSchedBtn_Click(object sender, EventArgs e)
        {
            new BaseEditor().Show();
        }

        private void updatedSchedBtn_Click(object sender, EventArgs e)
        {
            new UpdatesEditor().Show();
        }
    }
}
