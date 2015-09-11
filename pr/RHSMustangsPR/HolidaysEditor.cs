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
    public interface AddHolidayListener
    {
        void holidayAdded(Holiday holiday);
    }

    public partial class HolidaysEditor : Form, AddHolidayListener
    {
        Holidays holidays;
        
        public HolidaysEditor()
        {
            InitializeComponent();

            holidays = new Holidays();
        }

        private void addHolidayBtn_Click(object sender, EventArgs e)
        {
            new AddHoliday(this).Show();
        }

        private void saveHolidaysBtn_Click(object sender, EventArgs e)
        {
            holidays.saveToParse();
        }

        void AddHolidayListener.holidayAdded(Holiday d)
        {
            holidaysList.Items.Add(d.ToString());
            holidays.mHolidays.Add(d);
        }
    }
}
