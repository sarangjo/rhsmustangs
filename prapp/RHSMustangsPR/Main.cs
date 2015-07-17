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
    public partial class Main : Form
    {
        public Main()
        {
            InitializeComponent();
        }

        private void addSchedule_Click(object sender, EventArgs e)
        {
            AddSchedule s = new AddSchedule();
            s.Show();
        }
    }
}
