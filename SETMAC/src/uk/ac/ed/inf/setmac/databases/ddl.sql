CREATE TABLE Site (
  Id INTEGER NOT NULL PRIMARY KEY,
  Name TEXT NOT NULL UNIQUE
)
GO

CREATE TABLE Post (
  SiteId INTEGER NOT NULL REFERENCES Site,
  Id INTEGER NOT NULL,
  PostTypeId INTEGER NOT NULL,
  ParentId INTEGER,
  --AcceptedAnswerId INTEGER,
  --CreationDate NUMERIC NOT NULL,
  --Score INTEGER NOT NULL,
  --ViewCount INTEGER,
  Body BLOB,
  --OwnerUserId INTEGER,
  --LastEditorUserId  INTEGER,
  --LastEditorDisplayName TEXT,
  --LastEditDate NUMERIC,
  --LastActivityDate NUMERIC NOT NULL,
  --CommunityOwnedDate NUMERIC,
  ClosedDate NUMERIC,
  Title TEXT,
  Tags TEXT,
  --AnswerCount INTEGER,
  --CommentCount INTEGER,
  --FavoriteCount INTEGER,
  IsTest NUMERIC,
  PRIMARY KEY (
    Id,
    SiteId
  ),
  FOREIGN KEY (
    ParentId,
    SiteId
  )
  REFERENCES Post (
    Id,
    SiteId
  )
)
GO

CREATE TABLE PostHistory (
  SiteId INTEGER NOT NULL,
  PostId INTEGER NOT NULL,
  Id INTEGER NOT NULL,
  PostHistoryTypeId INTEGER NOT NULL,
  --RevisionGUID TEXT NOT NULL,
  CreationDate NUMERIC NOT NULL,
  --UserId INTEGER,
  --UserDisplayName TEXT,
  --Comment TEXT,
  Text BLOB,
  CloseReasonId INTEGER,
  PRIMARY KEY (
    Id,
    PostId,
    SiteId
  )
  FOREIGN KEY (
    PostId,
    SiteId
  )
  REFERENCES Post (
    Id,
    SiteId
  )
)
GO

CREATE TABLE Comment (
  SiteId INTEGER NOT NULL,
  PostId INTEGER NOT NULL,
  Id INTEGER NOT NULL,
  --Score INTEGER,
  Text BLOB NOT NULL,
  --CreationDate NUMERIC NOT NULL,
  --UserId INTEGER,
  PRIMARY KEY (
    Id,
    PostId,
    SiteId
  ),
  FOREIGN KEY (
    PostId,
    SiteId
  )
  REFERENCES Post (
    Id,
    SiteId
  )
)
GO
