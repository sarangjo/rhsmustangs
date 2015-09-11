using Parse;
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
    public interface AddDayListener
    {
        void dayAdded(Day day);
    }

    public partial class BaseEditor : Form, AddDayListener
    {
        /// <summary>
        /// Saves the schedule.
        /// </summary>
        public BaseSchedule schedule;

        public BaseEditor()
        {
            InitializeComponent();

            schedule = new BaseSchedule();
            schedule.LoadFromParse();
        }

        private void mondayBtn_Click(object sender, EventArgs e)
        {
            new AddDay(this, false, 2).Show();
        }

        private void tuesdayBtn_Click(object sender, EventArgs e)
        {
            new AddDay(this, false, 3).Show();
        }

        private void wednesdayBtn_Click(object sender, EventArgs e)
        {
            new AddDay(this, false, 4).Show();
        }

        private void thursdayBtn_Click(object sender, EventArgs e)
        {
            new AddDay(this, false, 5).Show();
        }

        private void fridayBtn_Click(object sender, EventArgs e)
        {
            new AddDay(this, false, 6).Show();
        }

        private void saveSchedule_Click(object sender, System.EventArgs e)
        {
            schedule.SaveToParse();
            Console.WriteLine("Saved to Parse");
        }

        void AddDayListener.dayAdded(Day day)
        {
            schedule.mDays[day.mDayOfWeek - 2] = day;
            if (day.saved)
            {
                savedDaysList.SetItemChecked(day.mDayOfWeek - 2, true);
            }
        }

        public static List<string> ItemsToList(ListBox.ObjectCollection listItems)
        {
            List<string> list = new List<string>();
            for (int i = 0; i < listItems.Count; i++)
            {
                list.Add(listItems[i].ToString());
            }
            return list;
        }
    }
}
