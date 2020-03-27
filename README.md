# Visitor-Management-App
  
Flow of Application:

1. Visitor will Click on Book `Book an Appointment` Button on Welcome Activity.

2. Then app will requst for a Picture of Visitor

3.  After taking picture, Visitor has to enter his/her own phone number on the screen. 
    There app will do checks for valid phone number (10 digits, etc).
    
4.  After initiating authentication if User Enters `OTP Wrong three` times then visitor's will marked as suspicious
    phone and picture will be stored in Database with Error Type ( Timeout or OTP_Invalid )
    4.1 If User doesn't enters OTP for 30 Seconds then there will be timeout and visitor will be stored in database.
    
5. After successfull authentication we will check if visitor is already present in database by using phone Number
    5.1 if user is already present then visitor will be moved to show data activity and His/her No of
        `Previous Visits` using a snackbar
    5.2 if user if not present in database then user will be moved to get_Data Activity and after entering 
        data user will be moved to show data activity.
        
6.  From show data Activity visitor can go Next and Search and Select Host whom he/she wants to meet and then after,
    confirming data meeting will be saved to database with some unique id, with date and Time.
    
===============================================================================================================================
For Hosts I have created three Hosts with Ids:- one,two three

What I can add More to Application:-

1. We can use Machine Learning to check if Visitor has provided his/her picuture or any dummy picture for avoiding
    his/her data in suspicious_visitor list
2. We can emplement direct host notification using Emails or SMS ( By using Twillio or any other Service ).
3. We can add another activity for showing Meetings Data sorted by Date.
4. Activity for adding employees.
5. Blacklisting any visitor.

=============================================================================================================================

If Firebase Auth doesn't work and show request blocked
Use Test Phone : `+91 123456790 OTP: 123456`
        

