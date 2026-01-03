-- Create goals table
CREATE TABLE goals (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    start_date DATE,
    deadline DATE,
    completion_date DATE,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT chk_goal_type CHECK (type IN ('LONG_TERM', 'MEDIUM_TERM', 'SHORT_TERM')),
    CONSTRAINT chk_goal_status CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'ON_HOLD'))
);

-- Create index on user_id for faster queries by user
CREATE INDEX idx_goals_user_id ON goals(user_id);

-- Create index on status for filtering
CREATE INDEX idx_goals_status ON goals(status);

-- Create index on type for filtering
CREATE INDEX idx_goals_type ON goals(type);

-- Create index on created_at for sorting
CREATE INDEX idx_goals_created_at ON goals(created_at);