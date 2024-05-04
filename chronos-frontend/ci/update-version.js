const fs = require("fs");
// const execSync = require("child_process").execSync;

const version = require("../package.json").version;
const buildDate = new Date().toISOString();
// const commitHash = execSync("git rev-parse --short HEAD").toString().trim();
// const branch = execSync("git rev-parse --abbrev-ref HEAD").toString().trim();

const content = `export const version = '${version}';
export const buildDate = '${buildDate}';`;

fs.writeFileSync("./src/environments/version.gen.ts", content);

console.log("Updated version!", {
  version,
  buildDate,
  // commitHash,
  // branch,
});
