namespace RHSMustangsPR
{
    partial class BaseEditor
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
            this.saveSchedule = new System.Windows.Forms.Button();
            this.mondayBtn = new System.Windows.Forms.Button();
            this.tuesdayBtn = new System.Windows.Forms.Button();
            this.wednesdayBtn = new System.Windows.Forms.Button();
            this.thursdayBtn = new System.Windows.Forms.Button();
            this.fridayBtn = new System.Windows.Forms.Button();
            this.savedDaysList = new System.Windows.Forms.CheckedListBox();
            this.SuspendLayout();
            // 
            // saveSchedule
            // 
            this.saveSchedule.Location = new System.Drawing.Point(160, 21);
            this.saveSchedule.Name = "saveSchedule";
            this.saveSchedule.Size = new System.Drawing.Size(147, 143);
            this.saveSchedule.TabIndex = 0;
            this.saveSchedule.Text = "Save";
            this.saveSchedule.UseVisualStyleBackColor = true;
            this.saveSchedule.Click += new System.EventHandler(this.saveSchedule_Click);
            // 
            // mondayBtn
            // 
            this.mondayBtn.Location = new System.Drawing.Point(22, 21);
            this.mondayBtn.Name = "mondayBtn";
            this.mondayBtn.Size = new System.Drawing.Size(75, 23);
            this.mondayBtn.TabIndex = 1;
            this.mondayBtn.Text = "Monday";
            this.mondayBtn.UseVisualStyleBackColor = true;
            this.mondayBtn.Click += new System.EventHandler(this.mondayBtn_Click);
            // 
            // tuesdayBtn
            // 
            this.tuesdayBtn.Location = new System.Drawing.Point(22, 51);
            this.tuesdayBtn.Name = "tuesdayBtn";
            this.tuesdayBtn.Size = new System.Drawing.Size(75, 23);
            this.tuesdayBtn.TabIndex = 2;
            this.tuesdayBtn.Text = "Tuesday";
            this.tuesdayBtn.UseVisualStyleBackColor = true;
            this.tuesdayBtn.Click += new System.EventHandler(this.tuesdayBtn_Click);
            // 
            // wednesdayBtn
            // 
            this.wednesdayBtn.Location = new System.Drawing.Point(22, 81);
            this.wednesdayBtn.Name = "wednesdayBtn";
            this.wednesdayBtn.Size = new System.Drawing.Size(75, 23);
            this.wednesdayBtn.TabIndex = 3;
            this.wednesdayBtn.Text = "Wednesday";
            this.wednesdayBtn.UseVisualStyleBackColor = true;
            this.wednesdayBtn.Click += new System.EventHandler(this.wednesdayBtn_Click);
            // 
            // thursdayBtn
            // 
            this.thursdayBtn.Location = new System.Drawing.Point(22, 111);
            this.thursdayBtn.Name = "thursdayBtn";
            this.thursdayBtn.Size = new System.Drawing.Size(75, 23);
            this.thursdayBtn.TabIndex = 4;
            this.thursdayBtn.Text = "Thursday";
            this.thursdayBtn.UseVisualStyleBackColor = true;
            this.thursdayBtn.Click += new System.EventHandler(this.thursdayBtn_Click);
            // 
            // fridayBtn
            // 
            this.fridayBtn.Location = new System.Drawing.Point(22, 141);
            this.fridayBtn.Name = "fridayBtn";
            this.fridayBtn.Size = new System.Drawing.Size(75, 23);
            this.fridayBtn.TabIndex = 5;
            this.fridayBtn.Text = "Friday";
            this.fridayBtn.UseVisualStyleBackColor = true;
            this.fridayBtn.Click += new System.EventHandler(this.fridayBtn_Click);
            // 
            // savedDaysList
            // 
            this.savedDaysList.FormattingEnabled = true;
            this.savedDaysList.Items.AddRange(new object[] {
            "M",
            "T",
            "W",
            "Th",
            "F"});
            this.savedDaysList.Location = new System.Drawing.Point(113, 55);
            this.savedDaysList.Name = "savedDaysList";
            this.savedDaysList.Size = new System.Drawing.Size(41, 79);
            this.savedDaysList.TabIndex = 6;
            // 
            // BaseEditor
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(323, 186);
            this.Controls.Add(this.savedDaysList);
            this.Controls.Add(this.fridayBtn);
            this.Controls.Add(this.thursdayBtn);
            this.Controls.Add(this.wednesdayBtn);
            this.Controls.Add(this.tuesdayBtn);
            this.Controls.Add(this.mondayBtn);
            this.Controls.Add(this.saveSchedule);
            this.Name = "BaseEditor";
            this.Text = "RHS Base Schedule Editor";
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button saveSchedule;
        private System.Windows.Forms.Button mondayBtn;
        private System.Windows.Forms.Button tuesdayBtn;
        private System.Windows.Forms.Button wednesdayBtn;
        private System.Windows.Forms.Button thursdayBtn;
        private System.Windows.Forms.Button fridayBtn;
        private System.Windows.Forms.CheckedListBox savedDaysList;
    }
}

