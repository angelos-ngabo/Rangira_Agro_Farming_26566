const { Provinces, Districts, Sectors, Cells, Villages } = require('./frontend/node_modules/rwanda');
const fs = require('fs');

const { v4: uuidv4 } = require('./frontend/node_modules/uuid');

const generateSeeds = () => {
  const BATCH_SIZE = 1000;
  let sql = 'TRUNCATE TABLE location CASCADE;\n';
  const records = [];

  const addRecord = (province, district, sector, cell, village) => {
    // Generate code
    const baseCode = `VIL-${village}-${cell}-${sector}`.toUpperCase().replace(/\s+/g, '');
    let code = baseCode.substring(0, 100);
    
    // Quick sanitization
    const p = province.replace(/'/g, "''");
    const d = district.replace(/'/g, "''");
    const s = sector.replace(/'/g, "''");
    const c = cell.replace(/'/g, "''");
    const v = village.replace(/'/g, "''");
    const cd = code.replace(/'/g, "''");
    
    // Generating UUID on the app side isn't strictly necessary if backend creates it,
    // but for SQL inserts without a default function we need md5 or we can use gen_random_uuid().
    // We'll use gen_random_uuid().
    
    records.push(`(gen_random_uuid(), '${cd}', '${p}', '${d}', '${s}', '${c}', '${v}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)`);
  };

  const provinces = Provinces();
  for (const province of provinces) {
    const pName = province === 'Kigali' ? province : province + ' Province';
    // Backend mapping uses East instead of Eastern Province, but let's stick to what rwanda returns
    const mappedProvince = province === 'East' ? 'Eastern Province' : 
                           province === 'North' ? 'Northern Province' : 
                           province === 'South' ? 'Southern Province' : 
                           province === 'West' ? 'Western Province' : province;

    const districts = Districts({ province }) || [];
    for (const district of districts) {
      const sectors = Sectors({ province, district }) || [];
      for (const sector of sectors) {
        const cells = Cells({ province, district, sector }) || [];
        for (const cell of cells) {
          const villages = Villages({ province, district, sector, cell }) || [];
          for (const village of villages) {
            addRecord(mappedProvince, district + ' District', sector + ' Sector', cell + ' Cell', village.trim());
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
