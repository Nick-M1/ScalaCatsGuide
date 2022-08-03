# Overview of Type Classes in Cats:
![](../../Resources/Overview_pic1.png)

### 1) The generic / abstract type-class, with its generic methods
![](../../Resources/Overview_pic2.png)

### 2) Implementations/instances of this type-class for 'concrete' data-types (e.g. for List, Option, Either)
Note: Below syntax ("implicit" keyword) is Scala 2
![](../../Resources/Overview_pic3.png)

Put all these implementations/instances into an object so it can be easily imported to other files
![](../../Resources/Overview_pic4.png)

Different syntax for the same thing: Implicits/Using-with or Context Bounds
![](../../Resources/Overview_pic5.png)

### 3) Laws that each type-class must follow
Note: Type-classes inherit both methods and laws from it's 'parent interfaces'
![](../../Resources/Overview_pic6.png)
