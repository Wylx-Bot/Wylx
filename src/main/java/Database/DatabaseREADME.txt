Wylx uses MongoDB as a database
The production database is not available for testing purposes, Wylx will break if it is not given a valid mongo database
In order to set up your own mongo server for testing go to https://www.mongodb.com/try and set up a free online database
Once your database is set up click connect -> connect your application -> uncheck the "include full driver code" box and copy the provided URL
Should look something like this: mongodb+srv://SnakeJ419:<password>@wylx.ojcnr.mongodb.net/myFirstDatabase?retryWrites=true&w=majority
Put this into .env with the key "MONGODB_URL"
NOTE: you will need to replace "<password>" in the url with the password you set during setup
After this you *should* be able to just run Wylx and have everything work, if you have questions please ask