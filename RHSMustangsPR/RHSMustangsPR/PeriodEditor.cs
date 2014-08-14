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
    public partial class PeriodEditor : Form
    {
        public AddSchedule addSched;

        public PeriodEditor(AddSchedule s)
        {
            InitializeComponent();
            addSched = s;

            //initializeGroupBoxes();
        }

        

        private void lunchButton_CheckedChanged(object sender, EventArgs e)
        {
            periodN.Enabled = !lunchButton.Checked;
        }

        private void periodButton_CheckedChanged(object sender, EventArgs e)
        {
            periodN.Enabled = periodButton.Checked;
        }

        private void addPeriodBtn_Click(object sender, EventArgs e)
        {
            String messageBoxText = "";
            int errors = 0;

            Period p = new Period();

            // First, checks all 5 fields.
            // 1 - Short
            if (!lunchRadio.Checked && !hrRadio.Checked)
            {
                if (!int.TryParse(periodNumberText.Text, out periodN) || periodN == 0)
                {
                    if (shortFormText.Text.Trim().Length <= 0)
                    {
                        errors++;
                        messageBoxText += "Please enter a valid period number.\n";
                    }
                }
                else if (shortFormText.Text.Trim().Length <= 0 || shortFormText.Text.Trim().Length > 2)
                {
                    errors++;
                    messageBoxText += "Short form text needs to be 1-2 characters.\n";
                }
            }
            // Parse integers
            bool intErrors = !(int.TryParse(startHr.Text, out startH) &&
                int.TryParse(startMin.Text, out startM) &&
                int.TryParse(endHr.Text, out endH) &&
                int.TryParse(endMin.Text, out endM));
            if (intErrors)
            {
                messageBoxText += "Please enter valid numbers for the start and end times.\n";
                errors++;
            }
            else
            {
                // 3/4 - Start/End Times
                if (startH > 24 || startH < 0 || endH > 24 || endH < 0 || startM > 60 || startM < 0 || endM > 60 || endM < 0)
                {
                    messageBoxText += "Please enter valid numbers for the start and end times.\n";
                    errors++;
                }
            }
            if (!aRadio.Checked && !bRadio.Checked && !cRadio.Checked && !allRadio.Checked)
            {
                messageBoxText += "Please select a lunch type.\n";
                errors++;
            }

            if (errors > 0)
            {
                MessageBox.Show(((errors == 1) ? ("ERROR:\n") : ("ERRORS:\n")) + messageBoxText);
            }
            else
            {
                // Creating the String
                String p = "";

                p += (periodN == 0) ? "" : (periodN + " ");
                if (lunchRadio.Checked)
                {
                    p += "L";
                    if (aRadio.Checked) p += "A ";
                    else if (bRadio.Checked) p += "B ";
                    else if (cRadio.Checked) p += "C ";
                    else if (allRadio.Checked) p += "N ";
                }
                else
                {
                    p += (hrRadio.Checked) ? "HR " : "";
                    p += shortFormText.Text.Trim() + " ";
                    if (customNameText.Text.Trim().Length > 0)
                        p += customNameText.Text.Trim() + " ";
                    else
                        p += "- ";
                }
                p += startH + " " + startM + " ";
                p += endH + " " + endM + " ";
                if (aRadio.Checked) p += "a";
                else if (bRadio.Checked) p += "b";
                else if (cRadio.Checked) p += "c";
                else if (allRadio.Checked) p += "0";

                addSched.addPeriod(p);
                this.Close();
            }
        }

        private void newGroupBtn_Click(object sender, EventArgs e)
        {
            if (!newGroupText.Text.Trim().Equals(""))
            {
                try
                {
                    groupsListBox.Items.Add(newGroupText.Text);
                    newGroupText.Text = "";
                }
                catch (Exception ex)
                {

                }
                
            }
        }

        /*private void addPeriodButton_Click(object sender, EventArgs e)
        {
            String messageBoxText = "";
            int errors = 0;
            int periodN = 0;
            int startH = 0, startM = 0, endH = 0, endM = 0;




            // First, checks all 5 fields.
            // 1 - Short
            if (!lunchRadio.Checked && !hrRadio.Checked)
            {
                if (!int.TryParse(periodNumberText.Text, out periodN) || periodN == 0)
                {
                    if (shortFormText.Text.Trim().Length <= 0)
                    {
                        errors++;
                        messageBoxText += "Please enter a valid period number.\n";
                    }
                }
                else if (shortFormText.Text.Trim().Length <= 0 || shortFormText.Text.Trim().Length > 2)
                {
                    errors++;
                    messageBoxText += "Short form text needs to be 1-2 characters.\n";
                }
            }
            // Parse integers
            bool intErrors = !(int.TryParse(startHr.Text, out startH) &&
                int.TryParse(startMin.Text, out startM) &&
                int.TryParse(endHr.Text, out endH) &&
                int.TryParse(endMin.Text, out endM));
            if (intErrors)
            {
                messageBoxText += "Please enter valid numbers for the start and end times.\n";
                errors++;
            }
            else
            {
                // 3/4 - Start/End Times
                if (startH > 24 || startH < 0 || endH > 24 || endH < 0 || startM > 60 || startM < 0 || endM > 60 || endM < 0)
                {
                    messageBoxText += "Please enter valid numbers for the start and end times.\n";
                    errors++;
                }
            }
            if (!aRadio.Checked && !bRadio.Checked && !cRadio.Checked && !allRadio.Checked)
            {
                messageBoxText += "Please select a lunch type.\n";
                errors++;
            }

            if (errors > 0)
            {
                MessageBox.Show(((errors == 1) ? ("ERROR:\n") : ("ERRORS:\n")) + messageBoxText);
            }
            else
            {
                // Creating the String
                String p = "";

                p += (periodN == 0) ? "" : (periodN + " ");
                if (lunchRadio.Checked)
                {
                    p += "L";
                    if (aRadio.Checked) p += "A ";
                    else if (bRadio.Checked) p += "B ";
                    else if (cRadio.Checked) p += "C ";
                    else if (allRadio.Checked) p += "N ";
                }
                else
                {
                    p += (hrRadio.Checked) ? "HR " : "";
                    p += shortFormText.Text.Trim() + " ";
                    if (customNameText.Text.Trim().Length > 0)
                        p += customNameText.Text.Trim() + " ";
                    else
                        p += "- ";
                }
                p += startH + " " + startM + " ";
                p += endH + " " + endM + " ";
                if (aRadio.Checked) p += "a";
                else if (bRadio.Checked) p += "b";
                else if (cRadio.Checked) p += "c";
                else if (allRadio.Checked) p += "0";

                sched.addPeriod(p);
                this.Close();
            }

        }

        private void lunchRadio_CheckedChanged(object sender, EventArgs e)
        {
            if (lunchRadio.Checked)
            {
                periodNumberText.Text = "";
                shortFormText.Text = "";
                customNameText.Text = "";
            }
        }
        private void hrRadio_CheckedChanged(object sender, EventArgs e)
        {
            if (hrRadio.Checked)
            {
                periodNumberText.Text = "";
                shortFormText.Text = "";
                customNameText.Text = "";
            }
        }
        private void periodNumberText_Leave(object sender, EventArgs e)
        {
            if (periodNumberText.Text.Trim().Length > 0)
            {
                lunchRadio.Checked = false;
                hrRadio.Checked = false;
                shortFormText.Text = "";
                customNameText.Text = "";
            }
        }

        private void shortFormText_Leave(object sender, EventArgs e)
        {
            if (shortFormText.Text.Trim().Length > 0)
            {
                lunchRadio.Checked = false;
                hrRadio.Checked = false;
                periodNumberText.Text = "";
            }
        }

        private void customNameText_Leave(object sender, EventArgs e)
        {
            if (customNameText.Text.Trim().Length > 0)
            {
                lunchRadio.Checked = false;
                hrRadio.Checked = false;
                periodNumberText.Text = "";
            }

        }*/
    }
}
