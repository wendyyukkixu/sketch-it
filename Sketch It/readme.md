  Wendy Xu
  20631406 w85xu
  openjdk version "11.0.5" 2019-10-15
  macOS 10.14.5 (MacBook Pro 2017)

Additional feature implemented: Cut-copy-paste

Icons made by Freepik from wwww.flaticon.com
License: https://www.freepikcompany.com/legal#nav-flaticon-agreement

Disable conditions:
    - disables cut/copy/paste (or entire edit option) based on shape selection and clipboard
    - disables circle, line, rectangle drawing tool when a shape is selected
    - disables fill colour when a line or line tool is selected
    - disables line colour when fill/bucket tool is selected
    - disables both line and fill colours when erase tool is selected
    - disables line thickness and style options when erase or fill/bucket tool is selected

Personal touches:
    - User may delete a shape while on the selection tool and a shape is selected by hitting the backspace or delete key (did not have the DELETE key on my keyboard, but should work too)
    - File-load option prompts user to save if current drawing is unsaved before loading other drawing
    - File-quit option only prompts user to save if current drawing is unsaved 
    - File-options also have shortcuts (New: CTRL+N, Load: CTRL+L, Save: CTRL+S, Quit: CTRL+Q)
    - Edit-options also have shortcuts (Cut: CTRL+X, Copy: CTRL+C, Paste: CTRL+V)

Sample code used:
    - 7.MVC/1.MVC/hellomvc3/HelloMVC3.java
    - 5.Widgets/5.colorpicker/ColorPickerSample.java
    - 2.JavaFX/6.menubar/MenuDemo.java
