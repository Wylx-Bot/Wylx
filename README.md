## About
Welcome to Wylx!

This is a simple Discord Bot written in Java using the [JDA](https://github.com/discord-jda/JDA) Discord API.
## Features
- Wylx Settings:
  - Enable / Disable specific commands or groups of commands
  - Display current status and Wylx statistics
- Server Utilities:
  - Clean: Remove messages sent by Wylx
  - Clear: Remove a certain number of messages or until a specific message
  - Role Menus: Allow users to self assign roles by reacting with emojis to messages
- Music:
  - Play music from YouTube
  - Loop songs
  - Manage a playlist
  - Display currently playing song
- TTRPG and Math:
  - Simple math operations
  - Parse LaTEX formulas and generate images
  - Roll dice
  - Retrieve DND Spell Statistics
- Time Conversion:
  - Users can set their timezone
  - When a time is found in a message, display conversion to other timezones

## Deploying Wylx
### Use Our Wylx Instance
You can add our hosted instance of Wylx to your Discord server with this link: https://discord.com/api/oauth2/authorize?client_id=933557997328793691&permissions=277293853696&scope=bot

### Build Your Own Instance (Not Recomended)
Alternatively to build and run Wylx for yourself with your own hardware and database. Currently Wylx-Core is in development to provide a library that can be used to build your own Discord bots from the ground up, this will be the recomended way to use the database and command structures we have established. 

**Step 1:**
Set up a MongoDB Instance, this can be either hosted on your machine or using a free tier of MongoDB Cloud for smaller deployments. Save the URL, Username, and Password needed to access your database.

**Step 2:**
Clone this repository to the directory you want to run Wylx from.
`git clone git@github.com:Wylx-Bot/Wylx.git`

**Step 3:**
Set up environment variables. Wylx will look for the following environment variables, these can either be set in the environment Wylx runs in or placed in a `.env` file at the top level of your cloned Wylx repository.
| Variable Name | Default Value | Description |
| -------- | ------- | ------- |
| RELEASE | `false` | Tells Wylx if it is a development or release instance. Controls if Wylx will use the release or beta token and prefix |
| MONGODB_URL | `mongodb://localhost:27017` | The URL to access the MongoDB instance at, should include the username and password if one is required for the database |
| DISCORD_TOKEN | `None` | The bot token as provided by discord. |
| BETA_DISCORD_TOKEN | `None` | The bot token as provided by discord to be used the RELEASE variable is set to false |
| BETA_PREFIX | `None` | Overrides the default and configured prefixes for commands when the RELEASE variable is set to false with this string |

**Step 4:**
Build and run Wylx. Run the following command from the top level of the cloned Wylx repository. This may take longer on the first run as the dependencies need to be downloaded.
`./gradlew build run`