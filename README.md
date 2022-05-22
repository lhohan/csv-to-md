# csv to markdown convertor

A simple csv to markdown convertor for easier viewing of a csv.

## Status of project

Simple parsing of csv works.

Does not support quoted fields yet.

## Usage

Tool: `scala-cli`

Commands:

- Prints usage:
    - `scala-cli run main.scala csv-to-md.scala -- -h`
- Convert single file:
    - ` scala-cli run main.scala csv-to-md.scala -- -f test-data/csv-1.csv`
- Convert all csv files in a directory (`~/Downloads`):
    - `scala-cli run main.scala csv-to-md.scala -- -d ~/Downloads`    
- Run tests:
    - `scala-cli test . --watch`    
