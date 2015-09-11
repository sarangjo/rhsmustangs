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
    public partial class AddHoliday : Form
    {
        public Holiday holiday;
        public AddHolidayListener l;

        public AddHoliday(AddHolidayListener l)
        {
            InitializeComponent();
            this.l = l;
        }

        private void saveHolidayBtn_Click(object sender, EventArgs e)
        {
            holiday = new Holiday(startPicker.Value, endPicker.Value,
                nameText.Text);
            l.holidayAdded(holiday);
            Close();
        }
    }
}
