import fs from 'fs';
import { Rwanda } from './data-Rwanda/provinces/index';

const generateSeeds = () => {
  const BATCH_SIZE = 1000;
  let sql = 'TRUNCATE TABLE location CASCADE;\n';
  const records: string[] = [];

  const addRecord = (province: string, district: string, sector: string, cell: string, village: string) => {
    // Generate code
    const baseCode = `VIL-${village}-${cell}-${sector}`.toUpperCase().replace(/\s+/g, '');
    let code = baseCode.substring(0, 100);
    
    // Quick sanitization for SQL
    const p = province.replace(/'/g, "''");
    const d = district.replace(/'/g, "''");
    const s = sector.replace(/'/g, "''");
    const c = cell.replace(/'/g, "''");
    const v = village.replace(/'/g, "''");
    const cd = code.replace(/'/g, "''");
    
    records.push(`(gen_random_uuid(), '${cd}', '${p}', '${d}', '${s}', '${c}', '${v}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)`);
  };

  const provinces = Object.keys(Rwanda);
  for (const province of provinces) {
    // Add " Province" to match existing backend data expectations if it's not Kigali
    const mappedProvince = province === 'Kigali' ? province : province + ' Province';
    
    const districtsData = (Rwanda as any)[province];
    const districts = Object.keys(districtsData);
    
    for (const district of districts) {
      const sectorsData = districtsData[district];
      const sectors = Object.keys(sectorsData);
      
      for (const sector of sectors) {
        const cellsData = sectorsData[sector];
        const cells = Object.keys(cellsData);
        
        for (const cell of cells) {
          const villages = cellsData[cell] as string[];
          
          for (const village of villages) {
            // Include ' District', ' Sector', ' Cell' suffix if needed to match what generate_seeds.js did previously.
            // Wait, does the frontend or DB expect them with suffix?
            // generate_seeds.js did: district + ' District', sector + ' Sector', cell + ' Cell'
            // We will do the same to maintain compatibility.
            addRecord(
              mappedProvince, 
              district + ' District', 
              sector + ' Sector', 
              cell + ' Cell', 
              village.trim()
            );
          }
        }
      }
    }
  }

  // Insert in batches
  for (let i = 0; i < records.length; i += BATCH_SIZE) {
    const batch = records.slice(i, i + BATCH_SIZE);
    sql += `INSERT INTO location (id, code, province, district, sector, cell, village, created_at, updated_at) VALUES\n`;
    sql += batch.join(',\n') + ';\n';
  }

  fs.writeFileSync('seeds.sql', sql);
  console.log(`Generated seeds.sql with ${records.length} locations`);
};

generateSeeds();
