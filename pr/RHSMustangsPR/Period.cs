using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Parse;

namespace RHSMustangsPR
{
    public class Period
    {
        public static String OVERRIDE_DEFAULT = "-";

        public string periodShort;
        public string periodName;
        public int startH, startM, endH, endM;
        public int groupN;
        public String note = null;

        /// <summary>
        /// Returns the parsed String to then save to the updates file.
        /// </summary>
        public override String ToString()
        {
            return periodShort + " " + ((periodName == null) ? OVERRIDE_DEFAULT : periodName) + " " + startH + " " + startM + " " + endH + " " + endM + " " + groupN;
        }

        /// <summary>
        /// Converts the C# object to Parse.
        /// </summary>
        /// <returns></returns>
        public ParseObject toParseObject()
        {
            ParseObject obj = new ParseObject("Period");

            obj["short"] = periodShort;
            obj["name"] = periodName;
            obj["startHr"] = startH;
            obj["startMin"] = startM;
            obj["endHr"] = endH;
            obj["endMin"] = endM;
            obj["groupN"] = groupN;

            if (note != null)
                obj["note"] = note;

            return obj;
        }

        public static Period newFromParse(ParseObject period)
        {
            Period p = new Period();
            
            p.periodShort = (string)period["short"];
            p.periodName = (string)period["name"];
            p.startH = (int)period["startHr"];
            p.startM = (int)period["startMin"];
            p.endH = (int)period["endHr"];
            p.endM = (int)period["endMin"];
            p.groupN = (int)period["groupN"];
            p.note = (string)period["note"];

            return p;
        }
    }
}
