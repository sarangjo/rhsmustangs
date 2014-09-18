using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using System.Windows.Input;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using RHSMustangsSL.Resources;
using Microsoft.Phone.Tasks;

namespace RHSMustangsSL
{
    public partial class MainPage : PhoneApplicationPage
    {
        WebBrowserTask browser;

        // Constructor
        public MainPage()
        {
            InitializeComponent();

            // Sample code to localize the ApplicationBar
            //BuildLocalizedApplicationBar();
        }

        private void lwsdImage_Tapped(object sender, GestureEventArgs e)
        {
            browser = new WebBrowserTask();
            browser.Uri = new Uri("http://www.lwsd.org/school/rhs/Pages/default.aspx", UriKind.Absolute);
            browser.Show();
        }
        private void facebookImage_Tapped(object sender, GestureEventArgs e)
        {
            browser = new WebBrowserTask();
            browser.Uri = new Uri("http://facebook.com/redmondhighasb", UriKind.Absolute);
            browser.Show();
        }
        private void twitterImage_Tapped(object sender, GestureEventArgs e)
        {
            browser = new WebBrowserTask();
            browser.Uri = new Uri("http://twitter.com/redmondasb", UriKind.Absolute);
            browser.Show();
        }

        private async void launchTwitter() {
            Uri twitterUri = new Uri("");
            await Windows.System.Launcher.LaunchUriAsync(twitterUri);
        }

        private void instagramImage_Tapped(object sender, GestureEventArgs e)
        {
            browser = new WebBrowserTask();
            browser.Uri = new Uri("http://instagram.com/redmondasb", UriKind.Absolute);
            browser.Show();
        }

        // Sample code for building a localized ApplicationBar
        //private void BuildLocalizedApplicationBar()
        //{
        //    // Set the page's ApplicationBar to a new instance of ApplicationBar.
        //    ApplicationBar = new ApplicationBar();

        //    // Create a new button and set the text value to the localized string from AppResources.
        //    ApplicationBarIconButton appBarButton = new ApplicationBarIconButton(new Uri("/Assets/AppBar/appbar.add.rest.png", UriKind.Relative));
        //    appBarButton.Text = AppResources.AppBarButtonText;
        //    ApplicationBar.Buttons.Add(appBarButton);

        //    // Create a new menu item with the localized string from AppResources.
        //    ApplicationBarMenuItem appBarMenuItem = new ApplicationBarMenuItem(AppResources.AppBarMenuItemText);
        //    ApplicationBar.MenuItems.Add(appBarMenuItem);
        //}
    }
}