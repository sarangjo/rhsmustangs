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
    public partial class UpdatesEditor : Form, EditDayListener
    {
        Updates updates;
        
        public static LoadingForm form;

        public UpdatesEditor()
        {
            InitializeComponent();

            updates = new Updates();

            removeUpdateBtn.Enabled = editUpdateBtn.Enabled = false;
        }

        private void addUpdateBtn_Click(object sender, EventArgs e)
        {
            new EditDay(this, true, 0).Show();
        }

        private void saveUpdatesBtn_Click(object sender, EventArgs e)
        {
            UpdatesEditor.form = new LoadingForm();
            updates.save();
        }

        void EditDayListener.dayEdited(Day d, int i)
        {
            if (i >= 0 && i < updates.mUpdatedDays.Count)
            {
                // Edit
                updatedDaysList.Items[i] = ((UpdatedDay)d).ToString();
                updates.mUpdatedDays[i] = ((UpdatedDay)d);
            }
            else
            {
                // Add
                updatedDaysList.Items.Add(((UpdatedDay)d).ToString());
                updates.mUpdatedDays.Add((UpdatedDay)d);
            }
        }

        private void updatedDaysList_SelectedIndexChanged(object sender, EventArgs e)
        {
            removeUpdateBtn.Enabled = editUpdateBtn.Enabled = (updatedDaysList.SelectedIndex >= 0);
        }

        private void removeUpdateBtn_Click(object sender, EventArgs e)
        {
            updates.mUpdatedDays.RemoveAt(updatedDaysList.SelectedIndex);
            updatedDaysList.Items.RemoveAt(updatedDaysList.SelectedIndex);
            updatedDaysList.ClearSelected();
        }

        private void editUpdateBtn_Click(object sender, EventArgs e)
        {
            new EditDay(this, updates.mUpdatedDays[updatedDaysList.SelectedIndex], updatedDaysList.SelectedIndex).Show();
        }

        private void copyUpdateBtn_Click(object sender, EventArgs e)
        {
            UpdatedDay selected = updates.mUpdatedDays[updatedDaysList.SelectedIndex];
            UpdatedDay copy = selected.Copy();
        }
    }
}
