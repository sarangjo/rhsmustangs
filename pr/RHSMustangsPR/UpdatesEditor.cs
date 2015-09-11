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
    public partial class UpdatesEditor : Form, AddDayListener
    {
        Updates updates;
        
        public static LoadingForm form;

        public UpdatesEditor()
        {
            InitializeComponent();

            updates = new Updates();
        }

        private void addUpdateBtn_Click(object sender, EventArgs e)
        {
            new AddDay(this, true, 0).Show();
        }

        private void saveUpdatesBtn_Click(object sender, EventArgs e)
        {
            UpdatesEditor.form = new LoadingForm();
            updates.save();
        }

        void AddDayListener.dayAdded(Day d)
        {
            updatedDaysList.Items.Add(((UpdatedDay)d).ToString());
            updates.mUpdatedDays.Add((UpdatedDay)d);
        }
    }
}
