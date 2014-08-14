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
    public partial class AddSchedule : Form
    {
        public Schedule sched;

        public AddSchedule()
        {
            InitializeComponent();
            listBox1.Items.Clear();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            new PeriodEditor(this).Show();
        }

        public void addPeriod(String s) {
            listBox1.Items.Add(s);
        }
    }
}
