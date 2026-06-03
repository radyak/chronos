export type ErrorResponseDTO = {
    error: {
        timestamp: string;
        status: number;
        errors: ApiErrorDTO[];
        message: string;
        path: string;
    };
    // other fields are irrelevant
}

export type ApiErrorDTO = {
    field: string;
    constraint: string;
    message: string;
    arguments: {
        min: number;
        max: number;
        // TO BE CONTINUED
    };
};
