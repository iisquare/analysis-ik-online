
/** dict.quantifier indexes **/
db.getCollection("dict.quantifier").ensureIndex({
  "_id": NumberInt(1)
},[
  
]);

/** dict.quantifier indexes **/
db.getCollection("dict.quantifier").ensureIndex({
  "text": NumberLong(1)
},{
  "unique": true
});

/** dict.quantifier indexes **/
db.getCollection("dict.quantifier").ensureIndex({
  "identity": NumberLong(1)
},[
  
]);

/** dict.quantifier indexes **/
db.getCollection("dict.quantifier").ensureIndex({
  "time_create": NumberLong(1)
},[
  
]);

/** dict.quantifier indexes **/
db.getCollection("dict.quantifier").ensureIndex({
  "time_update": NumberLong(1)
},[
  
]);

/** dict.stopword indexes **/
db.getCollection("dict.stopword").ensureIndex({
  "_id": NumberInt(1)
},[
  
]);

/** dict.stopword indexes **/
db.getCollection("dict.stopword").ensureIndex({
  "text": NumberLong(1)
},{
  "unique": true
});

/** dict.stopword indexes **/
db.getCollection("dict.stopword").ensureIndex({
  "uniq_text": NumberLong(1)
},[
  
]);

/** dict.stopword indexes **/
db.getCollection("dict.stopword").ensureIndex({
  "time_create": NumberLong(1)
},[
  
]);

/** dict.stopword indexes **/
db.getCollection("dict.stopword").ensureIndex({
  "time_update": NumberLong(1)
},[
  
]);

/** dict.synonym indexes **/
db.getCollection("dict.synonym").ensureIndex({
  "_id": NumberInt(1)
},[
  
]);

/** dict.synonym indexes **/
db.getCollection("dict.synonym").ensureIndex({
  "text": NumberLong(1)
},{
  "unique": true
});

/** dict.synonym indexes **/
db.getCollection("dict.synonym").ensureIndex({
  "identity": NumberLong(1)
},[
  
]);

/** dict.synonym indexes **/
db.getCollection("dict.synonym").ensureIndex({
  "time_create": NumberLong(1)
},[
  
]);

/** dict.synonym indexes **/
db.getCollection("dict.synonym").ensureIndex({
  "time_update": NumberLong(1)
},[
  
]);

/** dict.word indexes **/
db.getCollection("dict.word").ensureIndex({
  "_id": NumberInt(1)
},[
  
]);

/** dict.word indexes **/
db.getCollection("dict.word").ensureIndex({
  "text": NumberLong(1)
},{
  "unique": true
});

/** dict.word indexes **/
db.getCollection("dict.word").ensureIndex({
  "identity": NumberLong(1)
},[
  
]);

/** dict.word indexes **/
db.getCollection("dict.word").ensureIndex({
  "time_create": NumberLong(1)
},[
  
]);

/** dict.word indexes **/
db.getCollection("dict.word").ensureIndex({
  "time_update": NumberLong(1)
},[
  
]);

/** dict.quantifier records **/

/** dict.stopword records **/

/** dict.synonym records **/

/** dict.word records **/

/** system.indexes records **/
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "_id_",
  "key": {
    "_id": NumberInt(1)
  },
  "ns": "lucene.dict.quantifier"
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "_id_",
  "key": {
    "_id": NumberInt(1)
  },
  "ns": "lucene.dict.synonym"
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "_id_",
  "key": {
    "_id": NumberInt(1)
  },
  "ns": "lucene.dict.stopword"
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "_id_",
  "key": {
    "_id": NumberInt(1)
  },
  "ns": "lucene.dict.word"
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "uniq_text",
  "key": {
    "text": NumberLong(1)
  },
  "unique": true,
  "ns": "lucene.dict.quantifier",
  "dropDups": NumberLong(1),
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_identity",
  "key": {
    "identity": NumberLong(1)
  },
  "ns": "lucene.dict.quantifier",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_create",
  "key": {
    "time_create": NumberLong(1)
  },
  "ns": "lucene.dict.quantifier",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_update",
  "key": {
    "time_update": NumberLong(1)
  },
  "ns": "lucene.dict.quantifier",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "uniq_text",
  "key": {
    "text": NumberLong(1)
  },
  "unique": true,
  "ns": "lucene.dict.stopword",
  "dropDups": NumberLong(1),
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_identity",
  "key": {
    "uniq_text": NumberLong(1)
  },
  "ns": "lucene.dict.stopword",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_create",
  "key": {
    "time_create": NumberLong(1)
  },
  "ns": "lucene.dict.stopword",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_update",
  "key": {
    "time_update": NumberLong(1)
  },
  "ns": "lucene.dict.stopword",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "uniq_text",
  "key": {
    "text": NumberLong(1)
  },
  "unique": true,
  "ns": "lucene.dict.word",
  "dropDups": NumberLong(1),
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "uniq_text",
  "key": {
    "text": NumberLong(1)
  },
  "unique": true,
  "ns": "lucene.dict.synonym",
  "dropDups": NumberLong(1),
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_identity",
  "key": {
    "identity": NumberLong(1)
  },
  "ns": "lucene.dict.synonym",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_identity",
  "key": {
    "identity": NumberLong(1)
  },
  "ns": "lucene.dict.word",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_create",
  "key": {
    "time_create": NumberLong(1)
  },
  "ns": "lucene.dict.synonym",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_create",
  "key": {
    "time_create": NumberLong(1)
  },
  "ns": "lucene.dict.word",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_update",
  "key": {
    "time_update": NumberLong(1)
  },
  "ns": "lucene.dict.word",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_update",
  "key": {
    "time_update": NumberLong(1)
  },
  "ns": "lucene.dict.synonym",
  "background": NumberLong(1)
});


/** dict.quantifier.suggest indexes **/
db.getCollection("dict.quantifier.suggest").ensureIndex({
  "_id": NumberInt(1)
},[
  
]);

/** dict.quantifier.suggest indexes **/
db.getCollection("dict.quantifier.suggest").ensureIndex({
  "text": NumberLong(1)
},{
  "unique": true
});

/** dict.quantifier.suggest indexes **/
db.getCollection("dict.quantifier.suggest").ensureIndex({
  "identity": NumberLong(1)
},[
  
]);

/** dict.quantifier.suggest indexes **/
db.getCollection("dict.quantifier.suggest").ensureIndex({
  "time_create": NumberLong(1)
},[
  
]);

/** dict.quantifier.suggest indexes **/
db.getCollection("dict.quantifier.suggest").ensureIndex({
  "time_update": NumberLong(1)
},[
  
]);

/** dict.stopword.suggest.suggest indexes **/
db.getCollection("dict.stopword.suggest.suggest").ensureIndex({
  "_id": NumberInt(1)
},[
  
]);

/** dict.stopword.suggest.suggest indexes **/
db.getCollection("dict.stopword.suggest.suggest").ensureIndex({
  "text": NumberLong(1)
},{
  "unique": true
});

/** dict.stopword.suggest.suggest indexes **/
db.getCollection("dict.stopword.suggest.suggest").ensureIndex({
  "uniq_text": NumberLong(1)
},[
  
]);

/** dict.stopword.suggest.suggest indexes **/
db.getCollection("dict.stopword.suggest.suggest").ensureIndex({
  "time_create": NumberLong(1)
},[
  
]);

/** dict.stopword.suggest.suggest indexes **/
db.getCollection("dict.stopword.suggest.suggest").ensureIndex({
  "time_update": NumberLong(1)
},[
  
]);

/** dict.synonym.suggest indexes **/
db.getCollection("dict.synonym.suggest").ensureIndex({
  "_id": NumberInt(1)
},[
  
]);

/** dict.synonym.suggest indexes **/
db.getCollection("dict.synonym.suggest").ensureIndex({
  "text": NumberLong(1)
},{
  "unique": true
});

/** dict.synonym.suggest indexes **/
db.getCollection("dict.synonym.suggest").ensureIndex({
  "identity": NumberLong(1)
},[
  
]);

/** dict.synonym.suggest indexes **/
db.getCollection("dict.synonym.suggest").ensureIndex({
  "time_create": NumberLong(1)
},[
  
]);

/** dict.synonym.suggest indexes **/
db.getCollection("dict.synonym.suggest").ensureIndex({
  "time_update": NumberLong(1)
},[
  
]);

/** dict.word.suggest indexes **/
db.getCollection("dict.word.suggest").ensureIndex({
  "_id": NumberInt(1)
},[
  
]);

/** dict.word.suggest indexes **/
db.getCollection("dict.word.suggest").ensureIndex({
  "text": NumberLong(1)
},{
  "unique": true
});

/** dict.word.suggest indexes **/
db.getCollection("dict.word.suggest").ensureIndex({
  "identity": NumberLong(1)
},[
  
]);

/** dict.word.suggest indexes **/
db.getCollection("dict.word.suggest").ensureIndex({
  "time_create": NumberLong(1)
},[
  
]);

/** dict.word.suggest indexes **/
db.getCollection("dict.word.suggest").ensureIndex({
  "time_update": NumberLong(1)
},[
  
]);

/** dict.quantifier.suggest records **/

/** dict.stopword.suggest.suggest records **/

/** dict.synonym.suggest records **/

/** dict.word.suggest records **/

/** system.indexes records **/
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "_id_",
  "key": {
    "_id": NumberInt(1)
  },
  "ns": "lucene.dict.quantifier.suggest"
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "_id_",
  "key": {
    "_id": NumberInt(1)
  },
  "ns": "lucene.dict.synonym.suggest"
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "_id_",
  "key": {
    "_id": NumberInt(1)
  },
  "ns": "lucene.dict.stopword.suggest.suggest"
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "_id_",
  "key": {
    "_id": NumberInt(1)
  },
  "ns": "lucene.dict.word.suggest"
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "uniq_text",
  "key": {
    "text": NumberLong(1)
  },
  "unique": true,
  "ns": "lucene.dict.quantifier.suggest",
  "dropDups": NumberLong(1),
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_identity",
  "key": {
    "identity": NumberLong(1)
  },
  "ns": "lucene.dict.quantifier.suggest",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_create",
  "key": {
    "time_create": NumberLong(1)
  },
  "ns": "lucene.dict.quantifier.suggest",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_update",
  "key": {
    "time_update": NumberLong(1)
  },
  "ns": "lucene.dict.quantifier.suggest",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "uniq_text",
  "key": {
    "text": NumberLong(1)
  },
  "unique": true,
  "ns": "lucene.dict.stopword.suggest.suggest",
  "dropDups": NumberLong(1),
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_identity",
  "key": {
    "uniq_text": NumberLong(1)
  },
  "ns": "lucene.dict.stopword.suggest.suggest",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_create",
  "key": {
    "time_create": NumberLong(1)
  },
  "ns": "lucene.dict.stopword.suggest.suggest",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_update",
  "key": {
    "time_update": NumberLong(1)
  },
  "ns": "lucene.dict.stopword.suggest.suggest",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "uniq_text",
  "key": {
    "text": NumberLong(1)
  },
  "unique": true,
  "ns": "lucene.dict.word.suggest",
  "dropDups": NumberLong(1),
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "uniq_text",
  "key": {
    "text": NumberLong(1)
  },
  "unique": true,
  "ns": "lucene.dict.synonym.suggest",
  "dropDups": NumberLong(1),
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_identity",
  "key": {
    "identity": NumberLong(1)
  },
  "ns": "lucene.dict.synonym.suggest",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_identity",
  "key": {
    "identity": NumberLong(1)
  },
  "ns": "lucene.dict.word.suggest",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_create",
  "key": {
    "time_create": NumberLong(1)
  },
  "ns": "lucene.dict.synonym.suggest",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_create",
  "key": {
    "time_create": NumberLong(1)
  },
  "ns": "lucene.dict.word.suggest",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_update",
  "key": {
    "time_update": NumberLong(1)
  },
  "ns": "lucene.dict.word.suggest",
  "background": NumberLong(1)
});
db.getCollection("system.indexes").insert({
  "v": NumberInt(1),
  "name": "idx_time_update",
  "key": {
    "time_update": NumberLong(1)
  },
  "ns": "lucene.dict.synonym.suggest",
  "background": NumberLong(1)
});
