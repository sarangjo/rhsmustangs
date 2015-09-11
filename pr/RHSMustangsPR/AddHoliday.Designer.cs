namespace RHSMustangsPR
{
    partial class AddHoliday
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
            this.startPicker = new System.Windows.Forms.DateTimePicker();
            this.endPicker = new System.Windows.Forms.DateTimePicker();
            this.label2 = new System.Windows.Forms.Label();
            this.saveHolidayBtn = new System.Windows.Forms.Button();
            this.nameText = new System.Windows.Forms.TextBox();
            this.label3 = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(110, 18);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(64, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Add Holiday";
            // 
            // startPicker
            // 
            this.startPicker.Location = new System.Drawing.Point(43, 81);
            this.startPicker.Name = "startPicker";
            this.startPicker.Size = new System.Drawing.Size(200, 20);
            this.startPicker.TabIndex = 1;
            // 
            // endPicker
            // 
            this.endPicker.Location = new System.Drawing.Point(43, 120);
            this.endPicker.Name = "endPicker";
            this.endPicker.Size = new System.Drawing.Size(200, 20);
            this.endPicker.TabIndex = 2;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(137, 104);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(16, 13);
            this.label2.TabIndex = 3;
            this.label2.Text = "to";
            // 
            // saveHolidayBtn
            // 
            this.saveHolidayBtn.Location = new System.Drawing.Point(78, 153);
            this.saveHolidayBtn.Name = "saveHolidayBtn";
            this.saveHolidayBtn.Size = new System.Drawing.Size(125, 23);
            this.saveHolidayBtn.TabIndex = 4;
            this.saveHolidayBtn.Text = "Save Holiday";
            this.saveHolidayBtn.UseVisualStyleBackColor = true;
            this.saveHolidayBtn.Click += new System.EventHandler(this.saveHolidayBtn_Click);
            // 
            // nameText
            // 
            this.nameText.Location = new System.Drawing.Point(88, 46);
            this.nameText.Name = "nameText";
            this.nameText.Size = new System.Drawing.Size(155, 20);
            this.nameText.TabIndex = 5;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(47, 49);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(35, 13);
            this.label3.TabIndex = 6;
            this.label3.Text = "Name";
            // 
            // AddHoliday
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(284, 208);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.nameText);
            this.Controls.Add(this.saveHolidayBtn);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.endPicker);
            this.Controls.Add(this.startPicker);
            this.Controls.Add(this.label1);
            this.Name = "AddHoliday";
            this.Text = "AddHoliday";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.DateTimePicker startPicker;
        private System.Windows.Forms.DateTimePicker endPicker;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Button saveHolidayBtn;
        private System.Windows.Forms.TextBox nameText;
        private System.Windows.Forms.Label label3;
    }
}