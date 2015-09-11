namespace RHSMustangsPR
{
    partial class HolidaysEditor
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.label1 = new System.Windows.Forms.Label();
            this.holidaysList = new System.Windows.Forms.ListBox();
            this.addHolidayBtn = new System.Windows.Forms.Button();
            this.saveHolidaysBtn = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(103, 9);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(77, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Holidays Editor";
            // 
            // holidaysList
            // 
            this.holidaysList.FormattingEnabled = true;
            this.holidaysList.Location = new System.Drawing.Point(32, 40);
            this.holidaysList.Name = "holidaysList";
            this.holidaysList.Size = new System.Drawing.Size(219, 95);
            this.holidaysList.TabIndex = 1;
            // 
            // addHolidayBtn
            // 
            this.addHolidayBtn.Location = new System.Drawing.Point(106, 141);
            this.addHolidayBtn.Name = "addHolidayBtn";
            this.addHolidayBtn.Size = new System.Drawing.Size(75, 23);
            this.addHolidayBtn.TabIndex = 2;
            this.addHolidayBtn.Text = "Add Holiday";
            this.addHolidayBtn.UseVisualStyleBackColor = true;
            this.addHolidayBtn.Click += new System.EventHandler(this.addHolidayBtn_Click);
            // 
            // saveHolidaysBtn
            // 
            this.saveHolidaysBtn.Location = new System.Drawing.Point(32, 180);
            this.saveHolidaysBtn.Name = "saveUpdatesBtn";
            this.saveHolidaysBtn.Size = new System.Drawing.Size(219, 23);
            this.saveHolidaysBtn.TabIndex = 3;
            this.saveHolidaysBtn.Text = "Save";
            this.saveHolidaysBtn.UseVisualStyleBackColor = true;
            this.saveHolidaysBtn.Click += new System.EventHandler(this.saveHolidaysBtn_Click);
            // 
            // HolidaysEditor
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(284, 218);
            this.Controls.Add(this.saveHolidaysBtn);
            this.Controls.Add(this.addHolidayBtn);
            this.Controls.Add(this.holidaysList);
            this.Controls.Add(this.label1);
            this.Name = "HolidaysEditor";
            this.Text = "UpdatesEditor";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.ListBox holidaysList;
        private System.Windows.Forms.Button addHolidayBtn;
        private System.Windows.Forms.Button saveHolidaysBtn;
    }
}