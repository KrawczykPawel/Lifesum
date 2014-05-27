Lifesum
=======

0. Instructions:

In order to search for food, tap "Search loupe icon" and type some query. It will start search, with every new character or if user clicks "search loupe icon" from keyboard.
In order to add food to database swipe food card in the "Search" view.
In order to delete food from database swipe food card in the "Stored" view.
In order to see more information about food click ("i"") icon.
In order to change view, swipe from left part of screen to open NavigationDrawer or click on App icon in action bar.

1. If I have more time:

- Write tests for functionalities.
- Make network checks and info for the user, when network is not available.
- Create grid layout as an alternative for the CardsLib.
- Add "+" and "delete" buttons to the cards, to make it more accessible.
- Create visual tutorial for the first use with arrows and application functionalities description.
- Add full nutrition information to the expand view and make sure, that the animation is smooth.
- Add search result count and displaing this information.
- Repair the bug with undo controller. Because I also implamented undo for adding/removing foods. It was working fine in following way:
After user swipe to add record, undo layout appers for few seconds with possibility to undo that action. The same for delete. It is shame to admit it, but while refactoring I made some mistake and undo bar, start to appear on application startup, therefore I commented it out, because I do not have time to repair it.

2. Why I use this Libraries and how I done it:

NETWORK:

While choosing network operations framework, I was basing on my experience with Android Volley framework, which fits my needs for this application:

See more: https://github.com/mcxiaoke/android-volley

I was using this library to get data from given endpoint by using request queue and JsonObjectRequest. I extended JsonObjectRequest class to my custom TokenRequest class, in order to add "Authorization" header with given token. I wasted some time here, because first token was not working, fortunately with second one, it was great. While having problem with this, I have tried the same code on another endpoint without and with Authorization  -> code were working fine. After that I tried the Lifesum endpoind in the internet browser (Firefox on Linux) and added "Authorization" header in "Modify Headers" plugin for web developers and I get "invalid_credential" error, therefore I ask for another token.

ORM:

While choosing ORM framework, I was basing on following stackoverflow article:
http://stackoverflow.com/questions/371538/any-good-orm-tools-for-android-development

I have chosen greenDao, because it has good maintenance, documentation, fast performance, small size and little RAM consumption.
See more: http://greendao-orm.com/

Next step was to create data model. I have used DaoGenerator project to create greenDao files. I have created list of food object attributes and data types and run project, which generates files into the "src-gen" directory in my project. After that, I only initialized Dao in main activity and was able to insert/delete records in one simple method call. I was guessing data types, basing on values from various search queries, which I have analyzed by the internet browser with "Modify Headers" plugin.

JSON parser:

While choosing JSON parser framework, I was basing on my experience with GSON framework, which fits my needs for this application:

See more: https://code.google.com/p/google-gson/

After that, I added "Serializable" annotations for the food object and use GSON to deserialize it and get value from json node. After that I have list od food data ready to present.

UI/UX:

While choosing UI/UX framework,I was basing on my experience with CardsUI library, which provides awesome card layouts and interesting animations. In general, I think that there is trend for using cards right now. They are or were available in various top apps like facebook, twitter and pinterest etc.

See more: https://github.com/gabrielemariotti/cardslib

Firstly I created simple list adapter with ViewHolder (FoodItemAdapter.java class). But I always want to make something cool, therefore I get CardsLib framework in action. For navigation I made NavigationDrawer, because in my opinion dashboard and tabs are legancy. I put SearchView into action bar, to avoid wasting space for search results. After that I created fragments for "Search" and "Stored" view with "BaseFragment" abstract class to put some common methods there. Finally I have created custom views for FoodCard and ExpandCard. Download some free icons from internet and create my app icon using AndroidAssetStudio.

See more:
http://romannurik.github.io/AndroidAssetStudio/icons-launcher.html

