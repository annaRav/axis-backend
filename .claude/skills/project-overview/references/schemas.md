# Database Schemas

## Planned Schema: PostgreSQL (axis-goal service)

### users
```sql
-- User profiles from Keycloak
CREATE TABLE users (
    id UUID PRIMARY KEY,  -- Keycloak 'sub' claim
    email VARCHAR(255) NOT NULL UNIQUE,
    preferred_username VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_email ON users(email);
```

**Note:** Authentication is handled by Keycloak. This table stores user profile data for application use.

### goals
```sql
CREATE TABLE goals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    goal_type VARCHAR(50) NOT NULL,  -- LONG_TERM, MEDIUM_TERM, SHORT_TERM
    status VARCHAR(50) NOT NULL,      -- NOT_STARTED, IN_PROGRESS, COMPLETED, ARCHIVED
    start_date DATE,
    target_date DATE,
    completion_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_goal_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_goal_user ON goals(user_id);
CREATE INDEX idx_goal_status ON goals(status);
CREATE INDEX idx_goal_type ON goals(goal_type);
```

---

## Planned Schemas: MongoDB (axis-content service - future)

### pages collection
```javascript
{
  _id: ObjectId,
  slug: String,           // URL-friendly identifier
  title: String,
  content: String,        // Markdown content
  author_id: UUID,        // Reference to Keycloak user
  organization_id: UUID,  // Reference to PostgreSQL organization
  category_ids: [ObjectId],
  tags: [String],
  is_published: Boolean,
  view_count: Number,
  created_at: ISODate,
  updated_at: ISODate
}

// Indexes
db.pages.createIndex({ slug: 1 }, { unique: true })
db.pages.createIndex({ organization_id: 1 })
db.pages.createIndex({ title: "text", content: "text" })
db.pages.createIndex({ tags: 1 })
```

### revisions collection
```javascript
{
  _id: ObjectId,
  page_id: ObjectId,
  version: Number,
  content: String,
  author_id: UUID,
  comment: String,        // Revision comment
  created_at: ISODate
}

// Index
db.revisions.createIndex({ page_id: 1, version: -1 })
```

### categories collection
```javascript
{
  _id: ObjectId,
  name: String,
  slug: String,
  organization_id: UUID,
  parent_id: ObjectId,    // For nested categories
  description: String
}
```

---

## Current Schema: MongoDB (axis-media service)

### media collection
```javascript
{
  _id: ObjectId,
  filename: String,
  original_name: String,
  mime_type: String,
  size: Number,           // bytes
  storage_path: String,   // S3 key
  thumbnail_path: String,
  uploader_id: UUID,
  organization_id: UUID,
  metadata: {
    width: Number,        // for images
    height: Number,
    duration: Number      // for video/audio
  },
  created_at: ISODate
}
```

---

## Key Conventions

- **UUID Primary Keys:** All PostgreSQL entities use UUID as primary key
- **Timestamps:** All tables include `created_at` and `updated_at` (automatically managed by Hibernate annotations)
- **Soft Deletes:** Use `active` boolean flag for organizations, not actual deletion
- **Foreign Keys:** Always include proper FK constraints with `ON DELETE CASCADE` where appropriate
- **Indexes:** Create indexes on frequently queried columns (slugs, foreign keys, search fields)
- **Naming:** Use snake_case for SQL, camelCase for MongoDB