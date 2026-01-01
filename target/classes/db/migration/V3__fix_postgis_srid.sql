-- Ensure PostGIS extension exists
CREATE EXTENSION IF NOT EXISTS postgis;

-- Insert SRID 4326 if it doesn't exist (Fix for "Cannot find SRID 4326" error)
INSERT INTO spatial_ref_sys (srid, auth_name, auth_srid, srtext, proj4text)
SELECT 4326, 'EPSG', 4326, 'GEOGCS["WGS 84",DATUM["WGS_1984",SPHEROID["WGS 84",6378137,298.257223563,AUTHORITY["EPSG","7030"]],AUTHORITY["EPSG","6326"]],PRIMEM["Greenwich",0,AUTHORITY["EPSG","8901"]],UNIT["degree",0.0174532925199433,AUTHORITY["EPSG","9122"]],AUTHORITY["EPSG","4326"]]', '+proj=longlat +datum=WGS84 +no_defs'
WHERE NOT EXISTS (SELECT 1 FROM spatial_ref_sys WHERE srid = 4326);
