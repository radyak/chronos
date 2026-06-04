export interface EntryDTO {
    elementId: string;
    labels: string[];
    attributes: Record<string, any>;
    _meta?: {
        createAuthor?: string;
        createDate?: string;
        lastUpdateAuthor?: string;
        lastUpdateDate?: string;
        version?: number;
    }
}