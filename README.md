# evilnotchlib
this is a library with minecraft and some forge bug fixes. It allows modders to mod with ease and a relection MCPMappings Api to allow for reflection setting and getting objects and set final objects as well. This is a powerful lightweight library

Embeded Libraries:
```
Simple JSON(With Modifications): https://github.com/fangyidong/json-simple
IItemRenderer(re-writen) originally by elix_x found here: https://github.com/Elix-x/IItem-Renderer
ObfHelper & MCWriter: by squeek502 found here:https://github.com/squeek502/ASMHelper/tree/1.10.x/raw/squeek/asmhelper
```

Bug Fixes:
```
vanilla eggs on spawners
uuid fix(if uuid doesn't match server patch it)
uuid fix single player(if you give another person your world you get the right playerdata)
fixes forge parsing playerdata files twice on login
TileEntityFurnace(increase from short to int,fixed not properly reading/writing currentItemBurnTime)
GuiFurnace(fixed gui going out of bounds of integer thus displaying data wrong)
notch drops apples again
ItemToolTip Enchantment Fix(if text breaks manually uses enchantment name and roman numeral generator or integer)
Fixed Player Head Being on backwards when teleporting to them
Fixed Player shoulders not syning when using /tp,teleport,tpdim
Fixed Packets of blockstates being sent to the client from the main hand when 
placing a block in the offhand causing pig issues with silkspawners, and any other tile entities like signs

BlockEntityTag fixes:
sync client changes on the same tick rather then later,don't have to be in creative
make sure client that placed the spawner ignores the next packet 
if succesfull sent to it in case stuff is random
no more pig spawners after one spawnage
```

Features:
```
Capability System unlike forges mine is easy to use and understand as well as an ICapTick version for all objects that can tick
MCPMappings and ReflectionUtil API: ability to get and set objects with ease
Menu Lib(split from mod in 1.2.3 release): allows for modders to register their menu for multiple menu browsing
IItemRenderer(split from mod in 1.2.3 release)
BlockAPI: set objects in blocks for coders
Basic MC Lib:ability to make modding easier automation for registration and lang
GeneralRegistry: registry for commands, sound types and other general stuffs regsiter stuff here for compatibility
EntityModRegistry: support SpawnListEntries with NBT mobs
ClientBlockPlaceEvent: fires on client side when player places block for client sync
Line Library: a powerful library for parsing lines in many forms "modid:block" = "custom parsing values"
ConfigBase: part of the line library as an api implementation for the line library
Primitive Obj: allows for object modifyable primitive values(byte,short,int,long,float,double,boolean)
PairObj: unlike the other Pair classes this one makes since and uses generics so you never have to type cast
JavaUtil: varius pure java utilities
CSVE: basic implementation of comma seperated values with a varible system in place
Json model generation(Basic MC Lib)
Lang generation(Basic MC Lib)
Dyanmic Tranlsation Event(let's you override ItemStack#getDisplayName())
TileStackSync Events fires on whatever sides that the tile data gets set to this usually includes both client and server player can be null if the event is fired from command block or dungeons.
Adds the seed to f3 again per world
ASMHelper allows you to: replace methods,replace classes, and other usefull stuffs as well as make it easier to do per line injections
remove methods,add methods, add fields, remove fields
PickEvent.Block(allows you to override what the block returns via middle click)
PickEvent.Entity(allows you to override what the entity returns via middle click)
FakeWorld allows you to instatiate entities before a main world is even loaded you will have to exception handle for broken entities
NBTPathAPI(Not Working Quite Yet) allows for deep comparsions of nbt based upon logic type as well as comparing has tags merging nbt and compiling back into normal nbt

Future:
Font Renderer
Lan Skin Host Fix
```

Instalation This as a Dependancy:
```
Install forge mdk mc version
Place a compiled or deobfuscated evil notch lib jar into the libs folder
Place the aceess transformer found in the met-inf folder into src/main/resources
run gradlew setupDecompWorkspace eclipse
Go into eclipse java build path and add the jar
```
