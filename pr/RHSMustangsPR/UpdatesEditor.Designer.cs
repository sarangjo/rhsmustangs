namespace RHSMustangsPR
{
    partial class UpdatesEditor
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
            this.updatedDaysList = new System.Windows.Forms.ListBox();
            this.addUpdateBtn = new System.Windows.Forms.Button();
            this.saveUpdatesBtn = new System.Windows.Forms.Button();
            this.removeUpdateBtn = new System.Windows.Forms.Button();
            this.editUpdateBtn = new System.Windows.Forms.Button();
            this.copyUpdateBtn = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(103, 9);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(77, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Updates Editor";
            // 
            // updatedDaysList
            // 
            this.updatedDaysList.FormattingEnabled = true;
            this.updatedDaysList.Location = new System.Drawing.Point(32, 25);
            this.updatedDaysList.Name = "updatedDaysList";
            this.updatedDaysList.Size = new System.Drawing.Size(219, 95);
            this.updatedDaysList.TabIndex = 1;
            this.updatedDaysList.SelectedIndexChanged += new System.EventHandler(this.updatedDaysList_SelectedIndexChanged);
            // 
            // addUpdateBtn
            // 
            this.addUpdateBtn.Location = new System.Drawing.Point(32, 154);
            this.addUpdateBtn.Name = "addUpdateBtn";
            this.addUpdateBtn.Size = new System.Drawing.Size(105, 23);
            this.addUpdateBtn.TabIndex = 2;
            this.addUpdateBtn.Text = "Add Update";
            this.addUpdateBtn.UseVisualStyleBackColor = true;
            this.addUpdateBtn.Click += new System.EventHandler(this.addUpdateBtn_Click);
            // 
            // saveUpdatesBtn
            // 
            this.saveUpdatesBtn.Location = new System.Drawing.Point(32, 180);
            this.saveUpdatesBtn.Name = "saveUpdatesBtn";
            this.saveUpdatesBtn.Size = new System.Drawing.Size(219, 23);
            this.saveUpdatesBtn.TabIndex = 3;
            this.saveUpdatesBtn.Text = "Save";
            this.saveUpdatesBtn.UseVisualStyleBackColor = true;
            this.saveUpdatesBtn.Click += new System.EventHandler(this.saveUpdatesBtn_Click);
            // 
            // removeUpdateBtn
            // 
            this.removeUpdateBtn.Location = new System.Drawing.Point(146, 154);
            this.removeUpdateBtn.Name = "removeUpdateBtn";
            this.removeUpdateBtn.Size = new System.Drawing.Size(105, 23);
            this.removeUpdateBtn.TabIndex = 4;
            this.removeUpdateBtn.Text = "Remove Update";
            this.removeUpdateBtn.UseVisualStyleBackColor = true;
            this.removeUpdateBtn.Click += new System.EventHandler(this.removeUpdateBtn_Click);
            // 
            // editUpdateBtn
            // 
            this.editUpdateBtn.Location = new System.Drawing.Point(146, 128);
            this.editUpdateBtn.Name = "editUpdateBtn";
            this.editUpdateBtn.Size = new System.Drawing.Size(105, 23);
            this.editUpdateBtn.TabIndex = 5;
            this.editUpdateBtn.Text = "Edit Update";
            this.editUpdateBtn.UseVisualStyleBackColor = true;
            this.editUpdateBtn.Click += new System.EventHandler(this.editUpdateBtn_Click);
            // 
            // copyUpdateBtn
            // 
            this.copyUpdateBtn.Location = new System.Drawing.Point(32, 128);
            this.copyUpdateBtn.Name = "copyUpdateBtn";
            this.copyUpdateBtn.Size = new System.Drawing.Size(105, 23);
            this.copyUpdateBtn.TabIndex = 6;
            this.copyUpdateBtn.Text = "Copy";
            this.copyUpdateBtn.UseVisualStyleBackColor = true;
            this.copyUpdateBtn.Click += new System.EventHandler(this.copyUpdateBtn_Click);
            // 
            // UpdatesEditor
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(284, 219);
            this.Controls.Add(this.copyUpdateBtn);
            this.Controls.Add(this.editUpdateBtn);
            this.Controls.Add(this.removeUpdateBtn);
            this.Controls.Add(this.saveUpdatesBtn);
            this.Controls.Add(this.addUpdateBtn);
            this.Controls.Add(this.updatedDaysList);
            this.Controls.Add(this.label1);
            this.Name = "UpdatesEditor";
            this.Text = "UpdatesEditor";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.ListBox updatedDaysList;
        private System.Windows.Forms.Button addUpdateBtn;
        private System.Windows.Forms.Button saveUpdatesBtn;
        private System.Windows.Forms.Button removeUpdateBtn;
        private System.Windows.Forms.Button editUpdateBtn;
        private System.Windows.Forms.Button copyUpdateBtn;
    }
}