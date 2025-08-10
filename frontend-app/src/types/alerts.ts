export interface AlertDoc {
  _id?: string;
  _rev?: string;
  is_official: boolean;
  issued_by: string;
  timestamp: string;
  emergency_level?: string;
  emergency_type: string;
  affected_area: string[];
  response_measures?: string[];
  keywords?: string[];
  note?: string;
}
