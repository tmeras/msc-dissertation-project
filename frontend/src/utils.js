
export function formatDate(date) {
    return (date?.length === 3) ? (date[2] + "/" + date[1] + "/" + date[0]) : null
}

function wait(duration) {
    return new Promise(resolve => setTimeout(resolve, duration))
  }