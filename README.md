# evilnotchlib
this is a library with minecraft and some forge bug fixes. It allows modders to mod with ease and a relection MCPMappings Api to allow for reflection setting and getting objects and set final objects as well. This is a powerful lightweight library

Embeded Libraries:
```
Simple JSON(With Modifications): https://github.com/fangyidong/json-simple
```

Bug Fixes:
```
vanilla eggs on spawners
uuid fix(if uuid doesn't match server patch it)
uuid fix single player(if you give another person your world you get the right playerdata)
TileEntityFurnace(increase from short to int)
fixes forge parsing playerdata files twice on login
notch drops apples again
future: Move too quickly and vechile checks removed since this is a modding enviorment
```

Features:
```
MCPMappings and ReflectionUtil API: ability to get and set objects with ease
Menu Lib: allows for modders to register their menu for multiple menu browsing
BlockAPI: set objects in blocks for coders
Player Capabilities: different from forge they are easy to regiser and use. Currently server side only
Basic MC Lib:ability to make modding easier automation for registration and lang future models to
GeneralRegistry: registry for commands, sound types and other general stuffs regsiter stuff here for compatibility
EntityModRegistry: support SpawnListEntries with NBT mobs
Client Block Place Event: fires on client side for client sync on block placing
Line Library: a powerful library for parsing lines in many forms "modid:block" = "custom parsing values"
ConfigBase: part of the line library as an api implementation for the line library
Primitive Obj: allows for object modifyable primitive values(byte,short,int,long,float,double,boolean)
CSVE: basic implementation of comma seperated values with a varible system in place

Future:
Font Renderer
NBTPathAPI
Dynamic Client Translations
Lan Skin Host Fix
```
