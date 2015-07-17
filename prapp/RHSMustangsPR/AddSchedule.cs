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
        public Schedule sched = new Schedule();

        public AddSchedule()
        {
            InitializeComponent();
            listBox1.Items.Clear();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            new PeriodEditor(this).Show();
        }

        public void addPeriod(Period s) {
            sched.addPeriod(s);
            listBox1.Items.Add(s.ToString());
        }

        public void saveGroups(ListBox.ObjectCollection items)
        {
            sched.groups = new List<string>();
            groupsList.Items.Clear();
            for (int i = 0; i < items.Count; i++)
            {
                sched.groups.Add((string)items[i]);
                groupsList.Items.Add((string)items[i] + "\n");
            }
        }
    }
}
